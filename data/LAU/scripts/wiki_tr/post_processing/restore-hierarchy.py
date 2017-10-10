import json, glob, os, csv, urllib

reference = {}
with open("tr-reference.csv") as f:
    reader = csv.reader(f, delimiter=",", quotechar='"')
    for x, row in enumerate(reader):
        if x == 0: continue
        reference[row[0].decode('utf-8')] = row[1]


nuts = {}
with open("../nuts.js") as js:
    nuts = json.load(js)

os.chdir("../data")
for f in glob.glob("*.js"):
    with open(f) as js:
        data = json.load(js)
        for key, val in data.iteritems():
            upper = "/wiki/" + key
            if upper in nuts:
                for district, municipalities in val.iteritems():
                    for municipality in municipalities:
                        if nuts[upper][1] in reference.keys(): 
                            print "\t".join([upper, district, municipality] + nuts[upper] + [reference[nuts[upper][1]]]).encode('utf-8')
                        else:
                            print u"WARNING [{}] not in reference. File: {}".format(nuts[upper][1], f).encode('utf-8')
            else:
                print u"WARNING {} not in nuts.js. File: {}".format(key, f).encode('utf-8')




