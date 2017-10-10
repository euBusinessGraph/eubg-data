import csv, sys

if len(sys.argv) != 2:
    print "Usage: python {} [tab separated file to split]".format(sys.argv[0])

tsv = sys.argv[1]

with open(tsv) as f:
    

