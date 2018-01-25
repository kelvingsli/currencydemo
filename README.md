# Currency Demo

This demo serves to showcase
- pulling of currency rate data from text files
- using executor service to help with concurrent file reads
- caching data into redis after first fold loads

## Deployment

Install [Maven] (https://maven.apache.org/) to run build the project. 

Use the following commands.

```
$ mvn clean install
$ java -jar target/currency-app-1.0.0.jar
```

Install [Redis] (https://redis.io/) and run locally with the default configuration

```
hostname: localhost
port: 6379
```

Default location where the application will read the text files is at

```
c:/data
```

This can be changed within the AppConfig file under the currency.config path.

## API Use

The following APIs are exposed by the application

### Get all currency rates on a specific date

The api can queried via
```
API: http://localhost:8080/date?date={date}
Sample: http://localhost:8080/date?date=2017-01-01
```
where 
- date is in the yyyy-MM-dd format

### Get conversion rate between 2 currencies

The api can be queried via
```
API: http://localhost:8080/compare?curr1={currency-1}&curr2={currency-2}&date={date}
Sample: http://localhost:8080/compare?curr1=OMR&curr2=SGD&date=2017-01-01
```
where 
- date is in the yyyy-MM-dd format
- curr1 & curr2 are 3 character currency codes

### Get currency rate for a currency across a date range

The api can be queried via
```
API: http://localhost:8080/range?curr={currency}&start={start-date}&end={end-date}
Sample: http://localhost:8080/range?curr=OMR&start=2017-01-01&end=2017-01-03
```
where 
- start-date & end-date are in the yyyy-MM-dd format
- curr is a 3 character currency code