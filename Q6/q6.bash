#!/bin/bash
 echo "compiling q6..."
 javac -cp  ../json-simple-1.1.1.jar q6.java
 echo "running q6 on normal non-verticalized training data..."
 java -cp ../json-simple-1.1.1.jar:. q6
 echo "output available in parse_trees.dat"

 echo "running q6 on verticalized training data..."
 java -cp ../json-simple-1.1.1.jar:. q6 vert
 echo "output available in parse_trees_vert.dat"
