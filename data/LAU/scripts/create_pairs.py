import csv, sys

inp = sys.argv[1]
out = sys.argv[2]

needed_codes = ["at", "be", "bg", "cy", "cz", "de", "dk", "ee", "el", "es", "fi", "fr", "hr", "hu", "ie", \
                "it", "lu", "lv", "mt", "nl", "pl", "pt", "ro", "se", "si", "sk", "uk"]

with open(out, "w") as out_f:
    with open(inp) as f:
        reader = csv.reader(f, delimiter=',', quotechar='"')
        for row in reader:
            if row[1] not in needed_codes:
                continue
            for lang in row[3].split(","):
                out_f.write(row[1] + "\t" + lang + "\n")

