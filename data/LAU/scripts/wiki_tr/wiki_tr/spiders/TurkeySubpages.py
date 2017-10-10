import scrapy, json
from pages import pages

class TurkeySubpages(scrapy.Spider):
    name = "turkey-subpages"

    def start_requests(self):
        urls = pages
        for url in urls:
            yield scrapy.Request(url="https://en.wikipedia.org" + url, callback=self.parse)

    def parse(self, response):
        lau = {}
        for x, row in enumerate(response.xpath('//table[@class="sortable wikitable"]/tr')):
            district = row.xpath("//tr[{}]/td[1]/text()".format(x + 1)).extract()
            municipality = row.xpath("//tr[{}]/td[2]/text()".format(x + 1)).extract()
            if len(district) == 0 or len(municipality) == 0: continue
            if district[0] in lau:
                lau[district[0]].append(municipality[0])
            elif len(municipality[0]) > 3 :
                lau[district[0]] = municipality
            
        file_name = response.url.split("/")[-1]
        with open("data/" + file_name + ".js", 'w') as f:
            json.dump({ file_name : lau}, f)



