/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
USA. */


AUDIOSERVE README

AudioServe is an interactive genetic algorithm for evolving sound synthesizers. It was craeted by Matthew Yee-King in 2000 as part of his MSc thesis. 


README created 01/06/2003 AudioServe created 20/08/2000 or thereabouts
AudioServe edited to support a local genome database 20/07/2003

This thing is GPL'd. 

Warning - this software can make unexpected loud and distorted noises,
so watch your cones and don't blame me if anything breaks. 


COMPILING

put the AudioServe directory in your classpath. If jsyn and java are set
up correctly, you should be able to type javac AudioServe.AudioServe to
compile it. 

RUNNING

note you now have to specify local or remote as a command line parameter 
and if you choose local you must also specify the location relative or 
absolute of a genome database file. Since my genome file parsing code is 
a bit sketchy, best you use the supplied file. 

-- general

If you have java+jsyn working, put the AudioServe directory in your 
classpath then type:

java AudioServe.AudioServe local|remote <genome file>

--on linux

(i386 only as jsyn is only available for i386). 

make sure your LD_LIBRARY_PATH includes the jsyn .so file.

put the AudioServe directory in your classpath.

if the rest of jsyn and java are set up,

java AudioServe.AudioServe remote|local genomes1.txt should run it. 

where genomes1.txt is the location of a genome database file. 

my awt code is a bit dodgy so you might not be able to read the labels 
on the buttons - well, you have the source code... 

--on windows

same sort of thing. But first you need to install linux and throw your
windows install to /dev/null 

--on macos/X

not too hard either. If you've got jsyn up and running, just put
AudioServe directory in your classpath and type java
AudioServe.AudioServe remote|local 
 
