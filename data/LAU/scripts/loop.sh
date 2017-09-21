#!/bin/bash

if [ $# -lt 5 ]
  then
    echo "Usage: bash loop.sh [TARQL file] [Country X lang codes] [data dir] [out dir] [file suffix] [delimiter (blank for tab)]"
    exit
fi

query=$1
langs=$2
root_folder=$3
out_folder=$4
suffix=$5
delimiter=$6

for file in ${root_folder}/*.csv 
do
    while read -r lang
    do
        country=$(echo $file | grep -o "\/[^.\/]*.csv" | sed "s/.csv//"|  sed "s/\///g")
        cntry_code=$( echo $lang | sed "s/ .*$//" )
        langs_code=$( echo $lang | sed "s/^.* //" )

        if [ "$country" != "$cntry_code" ];then continue;fi

        echo "Running for country: $country with language: $langs_code"
        cat ${query} | sed "s/\$country/$country/g" > .temp.tarql

        # handle exceptions in files
        if [[ $country == "it" ]];then
            sed -i "s/\?POP/\?POP_2015/g" .temp.tarql
        fi

        if [[ $country == "uk" ]];then
            sed -i "s/\?NUTS_3/\?NUTS3_13/g" .temp.tarql
        fi

        if [[ $country == "dk" ]];then
            sed -i "s/, ?LAU2_NAT_CODE/, ?LAU1_NAT_CODE, '-', ?LAU2_NAT_CODE/g" .temp.tarql
        fi

       sed -i "s/\$language/$langs_code/g" .temp.tarql

        if [[ $delimiter != "" ]];then
            tarql -d ${delimiter} .temp.tarql $file > ${out_folder}/${country}-${suffix}-${langs_code}.ttl
        else
            tarql -t .temp.tarql $file > ${out_folder}/${country}-${suffix}-${langs_code}.ttl
        fi
    done < $langs
done
rm -f .temp.tarql

