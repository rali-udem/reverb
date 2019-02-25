# A French Version of ReVerb

This package is an adaptation of ReVerb to the French language. It has been created
at [RALI](http://rali.iro.umontreal.ca/) (Université de Montréal) by 
[Philippe Langlais](http://www.iro.umontreal.ca/~felipe/) and his team.

The original ReVerb ([on GitHub](https://github.com/knowitall/reverb)) was developed 
by the following people at the 
[University of Washington's Turing Center](http://turing.cs.washington.edu/) as part of the 
[KnowItAll Project](http://www.cs.washington.edu/research/knowitall/). 

* Anthony Fader <http://www.cs.washington.edu/homes/afader>
* Michael Schmitz <http://www.schmitztech.com/>
* Robert Bart (rbart at cs.washington.edu)
* Janara Christensen <http://www.cs.washington.edu/homes/janara>
* Niranjan Balasubramanian <http://www.cs.washington.edu/homes/niranjan>
* Jonathan Berant <http://www.cs.tau.ac.il/~jonatha6>

It is worth noting that the French version does not
provide a confidence function for the produced triples.

# How to Use

By default, this package works exactly like the English version of ReVerb,
documented on the original GitHub repository 
[here](https://github.com/knowitall/reverb), and therefore produces 
English triples from English text.

However, additions have been made for the user wishing to produce French 
triples from French text.

## Command-line utility

Start by building ReVerb from source, using Apache Maven 
(<http://maven.apache.org>). 
Go in the `core` directory and run this command to download the required 
dependencies, compile, and create a  single executable jar file.

    mvn clean compile assembly:single

If all goes well, the file 
`target/reverb-core-fr-1.0.0-jar-with-dependencies.jar`
is produced and you can run the command line utility for French extraction by
running the script `./reverb-fr` and piping in input sentences, one per
line.

For instance,

    echo "La mairesse a été élue en 2017." | ./reverb-fr
    
will produce

	Arg1=La mairesse
	Rel=a été élue en
	Arg2=2017
	RelLemma=avoir être élire en
	RelCanon=être élire en
	
where arg1, the relation and arg2 are output, including a lemmatized version
and a simplified version of the relation.

It is possible to provide multiple sentences to the program, by piping in 
a file containing one sentence per line, e.g.

	./reverb-fr < input-file

The program uses the UTF-8 encoding.

## Programming

See the class `ca.umontreal.rali.reverbfr.FrenchReVerbApplication` 
for the source code for the program mentioned above, as well as an example on
how to use this package. Make sure that the instruction 
`ReverbConfiguration.setLocale(Locale.FRENCH);` precedes all 
extraction logic. This switches ReVerb from English to French. Without this,
ReVerb will expect English. Switching back to English within the same program
does not work.

# Help and Contact
For more information, please visit the ReVerb homepage at the University of 
Washington: <http://reverb.cs.washington.edu>.

Please contact [Philippe Langlais](http://www.iro.umontreal.ca/~felipe/) for
the adaptation to French.

The licenses for this package specifically forbid commercial use.

# Citing ReVerb
If you use ReVerb in your academic work, please cite ReVerb with the following 
BibTeX citation:

    @inproceedings{ReVerb2011,
      author =   {Anthony Fader and Stephen Soderland and Oren Etzioni},
      title =    {Identifying Relations for Open Information Extraction},
      booktitle =    {Proceedings of the Conference of Empirical Methods
                      in Natural Language Processing ({EMNLP} '11)},
      year =     {2011},
      month =    {July 27-31},
      address =  {Edinburgh, Scotland, UK}
    }

If you use the French adaptation, please also cite:

     @article {GottiLanglaisReverb2016,
       title = {From French Wikipedia to Erudit: A test case for cross-domain open information extraction},
       journal = {Computational Intelligence},
       year = {2016},
       keywords = {Entity classification, Named entities, Natural language processing, Open information extraction},
       doi = {10.1111/coin.12120},
       url = {https://onlinelibrary.wiley.com/doi/abs/10.1111/coin.12120},
       pdf = {http://rali.iro.umontreal.ca/rali/sites/default/files/publis/coin.12120.pdf},
       author = {Fabrizio Gotti and Philippe Langlais}
     }
    