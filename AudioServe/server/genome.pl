#!E:\Perl\bin\perl.exe
#
# serves requests from audioserve applet - supplies 
# genomes and receives genomes
#
# matt yee-king may 2000
#
#
#

$dataFile = "genomes.txt";
$name = 0;
$maxAngle = 1;
$maxRadius = 2;
$maxGridSize = 3;
$chromosome = 4;
$numberOfParameters = 5;

print"Content-type:text/html\n\n";

&sendGenome();

sub sendGenome(){

# read the file to a buffer
    &fileToBuffer();
# split the file to array of data sets
    &splitFile();
# split each dataset to its components
    &splitIndividuals();
# write an individual
    
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
	     
	 }
	 #print "\n\n$i  value= ".$individuals[$i][1];
     }
    $random = 0;
    while ($random<1){ $random = rand($totalDataSets);}
    print "\n random = $random";
    print "\n".$individuals[$random][$name];
    print "\n".$individuals[$random][$maxRadius];
    print "\n".$individuals[$random][$maxAngle];
    print "\n".$individuals[$random][$maxGridSize];
    print "\n".$individuals[$random][$chromosome];
}

sub dienice {
    my($msg) = @_;
    print "<h2>Error</h2>\n";
    print $msg;
    exit;
}
