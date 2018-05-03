import sys
import numpy as np


class Problem:
    def __init__(self):
        self.name = None
        self.dim = None
        self.distanceMatrix = None

    def load(self, file):
        with open(file) as f:
            cityList = []
            lines = f.readlines()
            self.name = lines[0].split(' ')[-1][:-1]
            self.dim = int(lines[3].split(' ')[-1][:-1])
            self.distanceMatrix = [[0 for x in range(self.dim)] for y in range(self.dim)]
            for line in lines[6:-1]:
                (strIndex, strX, strY) = line.split(' ')
                index = int(strIndex)
                x = float(strX)
                y = float(strY)
                cityList.append((index, x, y))
                for c in cityList[:-1]:
                    dist = int(round(np.sqrt((x - c[1]) * (x - c[1]) + (y - c[2]) * (y - c[2]))))
                    self.distanceMatrix[index - 1][c[0] - 1] = dist
                    self.distanceMatrix[c[0] - 1][index - 1] = dist

    def distance(self, fromCity, toCity):
        return self.distanceMatrix[fromCity - 1][toCity - 1]

    def getName(self):
        return self.name

    def getDim(self):
        return self.dim


class Tour:
    def __init__(self, problem):
        self.problem = problem
        self.tour = []

    def setTour(self, tour):
        self.tour = tour

    def write(self, file):
        with open(file, 'w') as f:
            f.write("NAME : %s\n" % self.problem.getName())
            f.write("TYPE : TOUR\n")
            f.write("DIMENSION : %d\n" % self.problem.getDim())
            f.write("TOUR_SECTION\n")
            for city in self.tour:
                f.write("%d\n" % (city + 1))
            f.write("-1\nEOF")

    def read(self, file):
        i = 0
        with  open(file) as f:
            lines = f.readlines();
            for l in lines:
                i += 1
                splits = l.split(':')
                if splits[0].strip() == "TOUR_SECTION":
                    break;
            for c in lines[i:-2]:
                self.tour.append(int(c.strip()) - 1)

                # print(self.tour)

    def check(self, claimedDistance):
        distance = 0
        # check if distance is correct

        for index in range(len(self.tour[:-1])):
            current = self.tour[index]
            to = self.tour[index + 1]
            distance += self.problem.distance(current, to)

        # add the last connection
        distance += self.problem.distance(self.tour[-1], self.tour[0])

        checkList = [False for x in range(self.problem.getDim())]
        for c in self.tour:
            checkList[c] = True
        result = reduce(lambda x, y: x and y, checkList, True)
        if not result:
            return result, "Incomplete tour, not all cities have been visited"

        # print(distance)
        if claimedDistance != distance:
            return False, "Claimed distance is wrong, computed: %d, claimed: %d" % (distance, claimedDistance)
        # check if exactly every city is there
        return True, "Tour is correct"


if __name__ == "__main__":
    sys.argv[0]
    problemFileIndex = 1;
    tourFileIndex = 2
    claimedDistanceIndex = 3
    if sys.argv[0] == "-c":
        problemFileIndex += 1
        tourFileIndex += 1
        claimedDistanceIndex += 1

    p = Problem()
    p.load(sys.argv[problemFileIndex])

    t2 = Tour(p)
    t2.read(sys.argv[tourFileIndex])
    # print(t2.tour)

    claimedDistance = int(sys.argv[claimedDistanceIndex])
    check = t2.check(claimedDistance)
    print(check[1])
