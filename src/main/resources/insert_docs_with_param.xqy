xquery version "1.0-ml";

import module namespace dls = "http://marklogic.com/xdmp/dls"
at "/MarkLogic/dls.xqy";

(:
declare variable $contents as document-node() external;
:)

declare variable $contents as json:object external;
declare variable $docUri as xs:string external;

dls:document-insert-and-manage(
      $docUri,
      fn:true(),
      xdmp:to-json($contents))
