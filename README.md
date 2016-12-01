# CCGChinese
##Semantic parsing

  Translating natural language sentences into computer executable logic forms to execute on a Knowledge Base
##Applied Combinatory Categorial Grammar (CCG)

  Based on several grammar rules, from training data, learning a dictionary where a lexical entry contains its syntactic category and meaning representation. New sentences can be parsed according to the dictionary and grammar rules.
  
  lexical entry: w ⊢ Syn : Sem 
      
  - words w
      
  - a syntactic category Syn
      
  - a logical form Sem
  
  <img src="https://github.com/jessicatsaon/CCGChinese/blob/master/parse_process.png" width="500">

## Query Knowledge Base  
  Converting logic form to SPARQL in order to find answers by querying the knowledge base. Using [Virtuoso-opensource](https://github.com/openlink/virtuoso-opensource) as the RDF database engine.

# UI demo

![alt tag](https://github.com/jessicatsaon/CCGChinese/blob/master/screenshot.png)
