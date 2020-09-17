# TSP-Solver
A project that aims to find a solution that tends to optimum solution to TSP (Travelling Salesman Problem) within Analysis of Algorithms course. This program uses nearest neighbor algorithm, 2-opt and 3-opt algorithms respectively.

## How It Works
Cities are created from *"input.txt"* file. Firstly program runs nearest neighbor algorithm for several times (this depends number of cities). After finding a route, multiple  2-opt algorithms runs over that route until the previous found optimum distance equals to current found optimum distance. If necessary, 3-opt algorithm runs for a once (if there are lots of cities, this part will be skipped because of running time increases enormously) and the final route has been set. The route is printed to *"output.txt"* .
