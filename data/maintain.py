fin = open("train", "r")
fout = open("train0", "w")

newlist = set()

line0 = " "
line1 = " "

while line0:
    line0 = fin.readline()
    line1 = fin.readline()
    fin.readline()

    if line0 not in newlist:
        newlist.add(line0)
        fout.write(line0)
        fout.write(line1)
        fout.write("\n")

fin.close()
fout.close()
