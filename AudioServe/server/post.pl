#!/usr/bin/perl


$dataFile = "genomes.txt";
$mode = $ENV{'QUERY_STRING'};

## these variables will be used to refer to the elements of the individual array
## -->individual[0][$name] refers to the name of the first individual
$name = 0;
$maxAngle = 1;
$maxRadius = 2;
$maxGridSize = 3;
$chromosome = 4;
$numberOfParameters = 5;

print "Content-type:text/html\n\n";
#srand();
#print "test";

if (!$mode eq ""){

    ## this is a request for genomes by GET if there is a query string
   
    &sendGenome();
}

if ($mode eq ""){

    ## this is a genome delivery by POST if there is no query string

    ## read the sent genomes details
    &getPost();
    ## apend the genome to the data file
    &saveGenomeToFile();
    ## send a confirmation back to the applet
    &sendConfirm();
}


sub sendGenome(){

# read the file to a buffer
    &fileToBuffer();
# split the file to array of data sets
    &splitFile();
# split each dataset to its components
    &splitIndividuals();
# write an individual to the client
    &writeIndividuals();
    
#print $ENV{'QUERY_STRING'};
#print "dataFile = ".$data[2];

}

sub fileToBuffer(){

## retrieves the data file and reads it into a buffer
    open(INF,$dataFile) or dienice("Couldn't open $dataFile for reading: $! \n");
    @data = <INF>;
    close(INF);
}

sub splitFile(){

## splits the file into an array of data sets. each contains these fields
## 1. name
## 2. maxAngle
## 3. maxGridSize
## 4. maxRadius
## 5. chromosome
    
## get the whole file to a string buffer
    $wholefile = join("",@data);
    @fileSections = split(/<!--SpLiTmArKeR--!>/, $wholefile);
}

sub splitIndividuals(){

#    @individuals[][];

## splits each data set into its fields so they can be sent to the applet
    $totalDataSets = scalar(@fileSections)-1;

    for($i = 1;$i<$totalDataSets;$i++){

	 @a = split(/\n/, $fileSections[$i]);
	 for ($c=0;$c<$numberOfParameters;$c++){
	     @b = split(/=/, $a[$c]);
	     $individuals[$i][$c] = $b[1];
#	     print "\n\n value= ".$b[1];
	 }
     }
}


sub writeIndividuals(){

## two modes
## 1. send a single, randomly selected genome
## 2. send a population of genomes

    if ($mode eq "single"){
	## make a random number between 0 and totalDataSets
	$random = 0;
	while ($random<1){ $random = rand($totalDataSets);}
	print "\n".$individuals[$random][$name];
	print "\n".$individuals[$random][$maxRadius];
	print "\n".$individuals[$random][$maxAngle];
	print "\n".$individuals[$random][$maxGridSize];
	print "\n".$individuals[$random][$chromosome];
#	print "genomes read from file ".$totalDataSets;
    }

    
    if ($mode eq "population"){

	
    }

}

sub saveGenomeToFile(){
    
    ## format the genome so its ready to be appended to the end of the genome file.
    &formatGenome();
    
    ## open the file and lock it
    &openAndLock();
    
    ## write the data
    &writeAndClose();
    
}

sub openAndLock{
    open(OUTF,">>$dataFile") or dienice("Couldn't open %dataFile for
writing: $!");

# This locks the file so no other CGI can write to it at the 
# same time...
    flock(OUTF,2);
# Reset the file pointer to the end of the file, in case 
# someone wrote to it while we waited for the lock...
    seek(OUTF,0,2);
}

sub writeAndClose{
    print OUTF $formattedGenome;
    close(OUTF);

}

sub formatGenome(){
    
    ## converts the info from the POST into a form in which it can be stored
    $formattedGenome = "\n\n<!--SpLiTmArKeR--!>name=$FORM{'name'}\nmaxAngle=$FORM{'maxAngle'}\nmaxGridSize=$FORM{'maxGridSize'}\nmaxRadius=$FORM{'maxRadius'}\nchromosome=$FORM{'chromosome'}";

}

sub sendConfirm(){
    print "genome submitted\n";
    print "$formattedGenome berk";
    foreach $key (keys(%FORM)) {
	print "$key = $FORM{$key}\n";
    }
    
}

sub getPost(){
    read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
    @pairs = split(/&/, $buffer);
    foreach $pair (@pairs) {
	($name, $value) = split(/=/, $pair);
	$value =~ tr/+/ /;
	$value =~ s/%([a-fA-F0-9][a-fA-F0-9])/pack("C", hex($1))/eg;
	$FORM{$name} = $value;
    }
    
}

sub dienice {
	my($msg) = @_;
	print "Error\n";
	print $msg;
	exit;
}
