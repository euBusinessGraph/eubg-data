# -*- coding: utf-8 -*-
import csv, sys

file1 = sys.argv[1]
delim = sys.argv[2]
deCol = int(sys.argv[3])
itCol = int(sys.argv[4])
frCol = int(sys.argv[5])

if delim == "TAB":
    delim = "\t"

def normalize_de(string):
    return string\
    .replace(u"Biel/Bienne", u"Biel (BE)")\
    .replace(u"Delémont", u"Delsberg")\
    .replace(u"Fribourg", u"Freiburg")\
    .replace(u"Genève", u"Genf")\
    .replace(u"Gruyères", u"Greyerz")\
    .replace(u"Courgevaux", u"Gurwolf")\
    .replace(u"Evilard", u"Leubringen")\
    .replace(u"Meyriez", u"Merlach")\
    .replace(u"Moutier", u"Münster (BE)")\
    .replace(u"Neuchâtel", u"Neuenburg")\
    .replace(u"La Neuveville", u"Neuenstadt")\
    .replace(u"Porrentruy", u"Pruntrut")\
    .replace(u"Sierre", u"Siders")\
    .replace(u"Sion", u"Sitten")\
    .replace(u"Saint-Imier", u"St. Immer")

def normalize_fr(string):
    return string\
    .replace(u"Ins", u"Anet")\
    .replace(u"Basel", u"Bâle")\
    .replace(u"Jaun", u"Bellegarde")\
    .replace(u"Bellinzona", u"Bellinzone")\
    .replace(u"Bern", u"Berne")\
    .replace(u"Burgdorf", u"Berthoud")\
    .replace(u"Biel/Bienne", u"Bienne")\
    .replace(u"Brig-Glis", u"Brigue-Glis")\
    .replace(u"Büchslen", u"Buchillon")\
    .replace(u"Erlach", u"Cerlier")\
    .replace(u"Gempenach", u"Champagny")\
    .replace(u"Gampelen", u"Champion")\
    .replace(u"Giffers", u"Chevrilles")\
    .replace(u"Kerzers", u"Chiètres (FR)")\
    .replace(u"Chur", u"Coire")\
    .replace(u"Gurmels", u"Cormondes")\
    .replace(u"Rechthalten", u"Dirlaret")\
    .replace(u"Twann-Tüscherz", u"Douanne-Daucher")\
    .replace(u"Saanen", u"Gessenay")\
    .replace(u"Glarus", u"Glaris")\
    .replace(u"Ligerz", u"Gléresse")\
    .replace(u"Grenchen", u"Granges (SO)")\
    .replace(u"Düdingen", u"Guin")\
    .replace(u"Schelten", u"La Scheulte")\
    .replace(u"Laufen", u"Laufon")\
    .replace(u"Leukerbad", u"Loèche-les-Bains")\
    .replace(u"Leuk", u"Loèche-Ville")\
    .replace(u"Lengnau (BE)", u"Longeau")\
    .replace(u"Lurtigen", u"Lourtens")\
    .replace(u"Luzern", u"Lucerne")\
    .replace(u"Muntelier", u"Montilier")\
    .replace(u"Murten", u"Morat")\
    .replace(u"Ulmiz", u"Ormey")\
    .replace(u"Pieterlen", u"Perles")\
    .replace(u"Plaffeien", u"Planfayon")\
    .replace(u"Raron", u"Rarogne")\
    .replace(u"St. Antoni", u"Saint-Antoine")\
    .replace(u"St. Stephan", u"Saint-Etienne")\
    .replace(u"St. Gallen", u"Saint-Gall")\
    .replace(u"St. Niklaus", u"Saint-Nicolas")\
    .replace(u"St. Ursen", u"Saint-Ours")\
    .replace(u"St. Silvester", u"Saint-Silvestre")\
    .replace(u"Salgesch", u"Salquenen")\
    .replace(u"Salvenach", u"Salvagny")\
    .replace(u"Schaffhausen", u"Schaffhouse")\
    .replace(u"Schwyz", u"Schwytz")\
    .replace(u"Solothurn", u"Soleure")\
    .replace(u"Tafers", u"Tavel")\
    .replace(u"Thun", u"Thoune")\
    .replace(u"Tentlingen", u"Tinterin")\
    .replace(u"Turtmann", u"Tourtemagne")\
    .replace(u"Varen", u"Varone")\
    .replace(u"Visp", u"Viège")\
    .replace(u"Münchenwiler", u"Villars-les-Moines")\
    .replace(u"Winterthur", u"Winterthour")\
    .replace(u"Zofingen", u"Zofingue")\
    .replace(u"Zug", u"Zoug")\
    .replace(u"Zürich", u"Zurich")

def normalize_it(string):
    return string\
    .replace(u"Appenzell", u"Appenzello")\
    .replace(u"Basel", u"Basilea")\
    .replace(u"Bern", u"Berna")\
    .replace(u"Brig-Glis", u"Briga-Glis")\
    .replace(u"Chur", u"Coira")\
    .replace(u"Fribourg", u"Friburgo")\
    .replace(u"Genève", u"Ginevra")\
    .replace(u"Glarus", u"Glarona")\
    .replace(u"Lausanne", u"Losanna")\
    .replace(u"Luzern", u"Lucerna")\
    .replace(u"St. Gallen", u"San Gallo")\
    .replace(u"Schaffhausen", u"Sciaffusa")\
    .replace(u"Simplon", u"Sempione")\
    .replace(u"Solothurn", u"Soletta")\
    .replace(u"Schwyz", u"Svitto")\
    .replace(u"Zug", u"Zugo")\
    .replace(u"Zürich", u"Zurigo")

with open(file1) as f:
    reader = csv.reader(f, delimiter=delim)
    for row in reader:
        new = []
        for col in row:
            new.append(col.decode('utf-8'))
        new[deCol] = normalize_de(new[deCol])
        new[itCol] = normalize_it(new[itCol])
        new[frCol] = normalize_fr(new[frCol])
        print delim.join(new).encode('utf-8')



