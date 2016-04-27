# CCGChinese

基于组合范畴语法(Combinatory Categorial Grammar)的中文语义分析(Semantic Parsing)
## Demo Usage

使用Eclipse可直接打开整个工程。

- ./src/experiment/DevTrain: 训练Parser。
- ./src/experiment/crossTest: 10折交叉验证。
- ./src/experiment/SingleTest: 对单句进行parse。
- ./src/experiment/TestUI: Demo界面。

首先运行DevTrain，训练Parser，得到Lexicon，输出到./congifure/final_lexicon。然后运行TestUI，出现测试界面，输入语句进行parse，可以得到parse过程、使用的词条和最终生成的逻辑表达式Lambda Calculus。

运行crossTest可以进行10折交叉验证。- 

标注方案及标注数据：

- ./data/types: 三元关系 <e, p, e> 中实体e类型。
- ./data/relations: 关系p。
- ./data/np_lexicon: 数据中出现的名词实体及其对应逻辑表达式。
- ./data/train: 训练数据，问题及对应逻辑表达式。



## Semantic Parsing
 
自然语句 -> 逻辑表达式(Lambda Calculus)
 
 ```
 语句: 西单有什么日本料理
 Lambda Calculus: (lambda $0 e (and (restaurant:t $0) (zone:t $0 西单:s) (label:t $0 日本料理:s)))
 即: λx.restaurant(x) ∧ zone(x, 西单) ∧ label(x, 日本料理)
 ```

## Combinatory Categorial Grammar

 - Lexicon
 
 Lexicon entry examples:
 
 ```
 西直门  :- NP : 西直门:s : 3 : 10.0
 附近 : S\NP/(S|NP) : (lambda $0 <e,t> (lambda $1 e (lambda $2 e (and ($0 $2) (zone:t $2 $1))))) : 5.40679052197842
 ```
 
 - Parsing Rules
 
 ```
 X/Y Y/Z => X/Z fcomp
 X/Y Y   => X   fapply
 Y\Z X\Y => X\Z bcomp
 Y   X\Y => X   bapply
 ```