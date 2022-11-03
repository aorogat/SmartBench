[<< Home](https://github.com/aorogat/Maestro)
# Maestro: Benchmark Generation
This document shows you how to configure Maestro to generate a new benchmark and what you expect from Maestro.
* __Run SmartBenchh.java__ 
* __Configure Maestro__ This is the first step after running Maestro. You have to configure the system as shown. Until now, Maestro supports 5 KGs, and we are working on adding more. If this is the 1st time to run the system, you need first to generate the lexicon that Maestro will use to generate the questions (select Y for all questions). This lexicon table is based on the items' (subject, predicate, and object) labels in the KG as well the equivalent predicates's representation from a text corpus. For now, Maestro support Wikipedia to extract these representation. If you would like to use another Text Corpus, please reimplement the method ```getNLSentences()``` in the class ```offLine.scrapping.wikipedia```.
![Image](Images/benhmark_generation_conf.PNG)
