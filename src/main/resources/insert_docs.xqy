xquery version "1.0-ml";

import module namespace dls = "http://marklogic.com/xdmp/dls"
at "/MarkLogic/dls.xqy";

let $contents := object-node {
"person" : object-node {
"firstName": "john"
, "lastName": "withman"
, "age": 19
}
}

let $contents2 := object-node {
"person" : object-node {
"firstName": "lovly"
, "lastName": "diaz"
, "age": 80
}
}


let $contents3 := object-node {
"person" : object-node {
"firstName": "merk"
, "lastName": "cotton"
, "age": 60
}
}

return (
  dls:document-insert-and-manage(
      "/dls/persons/john_withman.json",
      fn:true(),
      $contents)
  , dls:document-insert-and-manage(
      "/dls/persons/lovly_diaz.json",
      fn:true(),
      $contents2)
  , dls:document-insert-and-manage(
      "/dls/persons/merk_cotton.json",
      fn:true(),
      $contents3)
)