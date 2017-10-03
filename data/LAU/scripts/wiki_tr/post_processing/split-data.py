import csv, sys

if len(sys.argv) != 2:
    print "Usage: python {} [tab separated file to extract from]".format(sys.argv[0])

tsv = sys.argv[1]

out = set()
with open(tsv) as f:
    reader = csv.reader(f, delimiter="\t")
    for x, row in enumerate(reader):
        if x == 0: continue
        out.add((row[1].decode('utf-8'), row[2].decode('utf-8'), row[7].decode('utf-8')))

for row in out:
    print u"\t".join(row).encode('utf-8')


