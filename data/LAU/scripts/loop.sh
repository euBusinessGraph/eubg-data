query=$1
root_folder=$2
out_folder=$3
suffix=$4
delimiter=$5

for file in ${root_folder}/*.csv 
do
        country=$(echo $file | grep -o "\/[^.\/]*.csv" | sed "s/.csv//"|  sed "s/\///g")
        echo "Running for country: $country"
        cat ${query} | sed "s/{country}/$country/g" > .temp.tarql

        # handle exceptions in files
        if [[ $country == "it" ]];then
            sed -i.bak "s/\?POP/\?POP_2015/g" .temp.tarql
        fi

        if [[ $country == "uk" ]];then
            sed -i.bak "s/\?NUTS_3/\?NUTS3_13/g" .temp.tarql
        fi

        if [[ $country == "dk" ]];then
            sed -i.bak "s/, ?LAU2_NAT_CODE/, ?LAU1_NAT_CODE, '-', ?LAU2_NAT_CODE/g" .temp.tarql
        fi

        if [[ $delimiter != "" ]];then
            tarql -d ${delimiter} .temp.tarql $file > ${out_folder}/${country}-${suffix}.ttl
        else
            tarql -t .temp.tarql $file > ${out_folder}/${country}-${suffix}.ttl
        fi
done
rm -f .temp.tarql

