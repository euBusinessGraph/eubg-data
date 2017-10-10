import scrapy, json


class QuotesSpider(scrapy.Spider):
    name = "turkey-lau"

    def start_requests(self):
        urls = [
            'https://en.wikipedia.org/wiki/List_of_municipalities_in_Turkey'
        ]
        for url in urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        data = {}
        for x, row in enumerate(response.xpath('//table[@class="wikitable"]/tr')):
            link = row.xpath("//tr[{}]/td[1]/a/@href".format(x + 1)).extract()
            provinces = row.xpath("//tr[{}]/td[1]/a/text()".format(x + 1)).extract()
            lists = row.xpath("//tr[{}]/td[2]/a/@href".format(x + 1)).extract()
            if len(provinces) == 0 or len(lists) == 0: 
                print "WARNING: not found list/province. For [{}]".format(row)
                continue
            data[lists[0]] = [link[0], provinces[0] ]
        with open("nuts.js", 'w') as f:
            json.dump(data, f)
