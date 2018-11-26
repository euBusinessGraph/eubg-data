#! /usr/bin/env python

import sys
import argparse

arg_parser = argparse.ArgumentParser()
arg_parser.add_argument(
    'input',
    help='TTL file to "orderise". Default is STDIN.',
    default=None
)
arg_parser.add_argument(
    '--ordering',
    help='File with explicit ordering. Default sets order incrementally.',
    default=None,
    type=str,
)
args = arg_parser.parse_args()

properties = []
seen_properties = set()
if args.ordering is not None:
    with open(args.ordering, 'r') as fd:
        for line in fd:
            clean_line = line.strip()
            if len(clean_line) == 0 or clean_line.startswith('#'):
                continue
            properties.append(clean_line.split(' ')[0])

    def retrieve_order(property):
        try:
            return properties.index(property)
        except ValueError:
            print('WARNING: Property {} not found in {}'.format(
                property, args.ordering
            ), file=sys.stderr)
            return None
else:
    def retrieve_order(property):
        try:
            return properties.index(property)
        except ValueError:
            properties.append(property)
            return len(properties) - 1


def is_natural(token):
    try:
        token_i = int(token)
        token_f = float(token)
        if str(token_i) == str(token_f):
            return False
        else:
            return token_i >= 0
    except ValueError:
        return False


if args.input is not None:
    fd = open(args.input, 'r')
else:
    fd = sys.stdin

order_triples = []
last_not_empty = None
with fd:
    for line in fd:
        order = None
        clean_line = line.strip()
        tokens = [t for t in line.strip().split(' ') if t != '']
        last_not_empty = len(clean_line) > 0
        if len(tokens) > 1 and tokens[1] == 'a':
            if (len(tokens) == 3 and clean_line.endswith(';')):
                property_type = tokens[2][:-1]
            elif (len(tokens) == 4 and tokens[-1] == ';'):
                property_type = tokens[2]
            else:
                property_type = None
            if property_type in {'owl:DatatypeProperty', 'owl:ObjectProperty'}:
                property = tokens[0]
                if property in seen_properties:
                    print('INFO: Property {} already has an order'.format(
                        property
                    ), file=sys.stderr)
                else:
                    seen_properties.add(property)
                order = retrieve_order(property)
        elif len(tokens) > 1 and tokens[0] == 'ebg:order':
            if (
                len(tokens) == 2 and clean_line.endswith(';')
            ):
                line_end = clean_line[-1]
                token = tokens[1][:-1]
            elif (
                len(tokens) == 3 and tokens[-1] in {';'}
            ):
                token = tokens[1]
            else:
                token = None
            if token and is_natural(token):
                continue
        elif (len(tokens) == 3 and clean_line.endswith('.')) or (len(tokens) == 4 and tokens[-1] == '.'):
            if tokens[1] == 'ebg:order':
                continue
        print(line.rstrip())
        if order is not None:
            order_triples.append((property, order))
            # print('  ebg:order {} ;'.format(order))

if order_triples:
    if last_not_empty:
        print('')
    for triple in sorted(order_triples, key=lambda x: x[1]):
        print('{} ebg:order {}.'.format(triple[0], triple[1]))
