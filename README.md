The dataset is in the resources folder, and the information about the instance ID and path is in the
parameter.yaml file. After running the program, you will be prompted to enter the ID of the dataset 
you want to calculate. Once the ID is entered, the calculation begins. The program will calculate 10 
times with different random seeds, each time running for 1000 seconds. During the iteration process, 
when the optimal solution is updated, the program will output the weight of the updated dominating tree. 
At the end of each run, it will output the weight of the minimum dominating tree obtained. If this weight
is equal to or better than the known optimal solution for this instance, the program will output the edge 
information of this tree obtained at this time. Additionally, once a known optimal solution for this 
instance is found in a calculation, that calculation will automatically end and start the next calculation 
without having to wait for 1000 seconds.