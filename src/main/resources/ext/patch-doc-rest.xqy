xquery version "1.0-ml";
module namespace patch-doc = "http://marklogic.com/rest-api/resource/ext/patch-doc-rest";

(:
import module namespace plugin = "http://marklogic.com/extension/plugin"
at "/MarkLogic/plugin/plugin.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";
declare option xdmp:mapping "false";
:)

declare function patch-doc:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
  ()
};

declare function patch-doc:put(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
  ()
};

declare function patch-doc:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()*
{
  ()
};

declare function patch-doc:delete(
    $context as map:map,
    $params  as map:map
) as document-node()?
{
  ()
};
