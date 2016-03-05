import re

fin = open("train", "r")
fout = open("constant", "w")

line = " "

while line:
    line = fin.readline()
    line = fin.readline()
    lis = line.split()

    for ii in lis:
        if re.match('[0-9]+:i', ii):
            continue
        if re.match('\$[0-9]', ii):
            continue
        for c in ii:
            if c == "(" or c == ")":
                continue;
            fout.write(c)
        fout.write(" ")
    fout.write("\n")
    line = fin.readline()

fin.close()
fout.close()
