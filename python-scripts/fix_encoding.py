#!/usr/bin/python

from chardet import detect

import os
import sys
import getopt


# ***********************
# File encoding functions
# ***********************

def get_encoding_type(file):
    with open(file, 'rb') as f:
        rawdata = f.read()
    return detect(rawdata)['encoding']


def correct_file_encoding(filename, newFilename, encoding_from, encoding_to='utf-8'):
    with open(filename, 'r', encoding=encoding_from) as fr:
        with open(newFilename, 'w', encoding=encoding_to) as fw:
            for line in fr:
                fw.write(line[:-1]+'\r')


# *************
# Main function
# *************
def main(argv):  
    input_file = ""
    output_file = ""

    try:
        opts, args = getopt.getopt(argv, "h:i:o:")
    except getopt.GetoptError:
        print("fix_encoding.py -i <input_file> -o <output_file>")
        sys.exit(2)
    for opt, arg in opts:
        if opt == "-h":
            print("fix_encoding -i <input_file> -o <output_file>")
            sys.exit()
        elif opt in ("-i"):
            input_file = arg
        elif opt in ("-o"):
            output_file = arg

    correct_file_encoding(input_file, output_file, encoding_from=get_encoding_type(input_file), encoding_to='utf-8')


# *****************
# Run main function
# *****************
if __name__ == "__main__": main(sys.argv[1:])
