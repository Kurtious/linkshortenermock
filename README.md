# Link Shortener App #


Link Shortener Service:

A light-weight Scalatra service for high performance link shortening and redirecting.
It utilises murmur3 Hash function to generate a shorter link(code) and buckets to avoid Hash conflicts.
The persistence is based on MongoDB, a high performance and flexibile solution allowing query easier over a set of documents without having to care of schemas. 

## Build & Run ##
I assume MongoDB is installed and accessible under 127.0.0.1:27017

```sh
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

On any Browser (domain = localhost:8080):
```
GET /
```
gives a short instruction on how to use the service

```
GET /shorten?url=$URL$
```
will return a JSON with e.g. { "link" : "0/a5263230" }

```
GET /:bucket/:code
```
will redirect to the previously stored long url.
prior to the redirection we store some basic data from the request header such as User-Agent, Accept-Language, IP and increment the click counter in MongoDB

for debuggin puroposes:

```
GET /table
```
here you see what is stored for the short link lookup

```
GET /tracking_table
```
here you can see the stored tracking records 

```
GET /records
```
will show in tabular form a simple and human readable version of the records

```
GET /retrieve?url=$URL$
```
will show all revelant analytics for that specific URL