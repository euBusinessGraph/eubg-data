import csv, sys

inp = sys.argv[1]
out = sys.argv[2]

needed_codes = ["at", "be", "bg", "cy", "cz", "de", "dk", "ee", "el", "es", "fi", "fr", "hr", "hu", "ie", \
                "it", "lu", "lv", "mt", "nl", "pl", "pt", "ro", "se", "si", "sk", "uk"]

with open(out, "w") as out_f:
    with open(inp) as f:
        reader = csv.reader(f, delimiter=',', quotechar='"')
        for row in reader:
            if row[0].strip() not in needed_codes:
                continue
            for lang in row[1].split(","):
                out_f.write(row[0].strip() + "\t" + lang.strip() + "\n")

