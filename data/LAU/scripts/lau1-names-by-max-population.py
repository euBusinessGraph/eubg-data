import csv, sys, pprint

input_file = sys.argv[1]

codes = {}
column_code = 0
column_name1 = 0
column_name2 = 0
column_population = 0
with open(input_file) as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    for x, row in enumerate(reader):
        if x == 0:
            for y, col in enumerate(row):
                if col == "LAU1_NAT_CODE":
                    column_code = y
                if col == "NAME_1":
                    column_name1 = y
                if col == "NAME_2_LAT":
                    column_name2 = y
                if col == "POP":
                    column_population = y
            continue
        current = {
                "NAME1": row[column_name1],
                "NAME2": row[column_name2],
                "POP": row[column_population]
            }
        try:
            int(row[column_population])
        except:
            continue
        if row[column_code] in codes:
                if int(row[column_population]) > int(codes[row[column_code]]["POP"]):
                    codes[row[column_code]] = current
        else:
            codes[row[column_code]] = current

print '{}\t{}\t{}\t{}'.format("LAU1", "NAME1", "NAME2", "POP")
for key, val in codes.iteritems():
    print "{}\t{}\t{}\t{}".format(key, val["NAME1"], val["NAME2"], val["POP"])

