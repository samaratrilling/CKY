 #!/bin/bash
 echo "compiling q4..."
 javac -cp  ../json-simple-1.1.1.jar q4.java
 echo "running q4 on normal non-verticalized training data..."
 echo "replacing rare words..."
 java -cp ../json-simple-1.1.1.jar:. q4
 echo "creating counts file..."
 rm replaced.counts
 python count_cfg_freq.py replacedRare.dat > cfg.counts
 echo "counts available in cfg.counts."

 echo "running q4 on verticalized training data..."
 java -cp ../json-simple-1.1.1.jar:. q4 vert
 echo "creating verticalized counts file..." 
 rm replacedVert.counts
 python count_cfg_freq.py replacedRareVert.dat > replacedVert.counts
 echo "counts available in replacedVert.counts" 
