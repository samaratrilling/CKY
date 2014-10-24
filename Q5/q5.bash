#!/bin/bash
 echo "compiling q5..."
 javac -cp  ../json-simple-1.1.1.jar q5.java
 echo "running q5 on normal non-verticalized training data..."
 java -cp ../json-simple-1.1.1.jar:. q5
 echo "output available in parse_trees.dat"

 echo "running q5 on verticalized training data..."
 java -cp ../json-simple-1.1.1.jar:. q5 vert
 echo "output available in parse_trees_vert.dat"
