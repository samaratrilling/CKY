#!/bin/bash
mkdir output

echo "running question4."
date
cd Q4
./q4.bash
scp replacedRare.dat ../output/
scp replacedRareVert.dat ../output/
scp replaced.counts ../output/
scp replacedVert.counts ../output/
cd ..
echo '\n'
echo "running question5."
date
cd Q5
./q5.bash
scp parse_trees.dat ../output/
scp parse_trees_vert.dat ../output/
cd ..
echo '\n'
echo "running question6."
date
cd Q6
./q6.bash
scp parse_trees.dat ../output/
scp parse_trees_vert.dat ../output/
echo "finished."
