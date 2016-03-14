# -*- coding: utf-8 -*-

in_src = open("src", "r")
in_trg = open("trg", "r")
in_t3 = open("t3", "r")
out_prob = open("prob", "w")

src_dic = {}
trg_dic = {}

src = in_src.readlines()
for line in src:
    src_dic[line.split()[0]] = line.split()[1]
# print src_dic
trg = in_trg.readlines()
for line in trg:
    trg_dic[line.split()[0]] = line.split()[1]
# print trg_dic
t3 = in_t3.readlines()
for line in t3:
    id1 = line.split()[0]
    id2 = line.split()[1]
    sta = line.split()[2]
    if (id1 in src_dic.keys() and id2 in trg_dic.keys()):
        out_prob.write(src_dic[id1] + "  ::  ")
        out_prob.write(trg_dic[id2] + "  ::  ")
        out_prob.write(sta)
        out_prob.write("\n")

in_src.close()
in_trg.close()
in_t3.close()
out_prob.close()
