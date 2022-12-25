## Configuration Documentation for Maestro System

The Maestro system has a number of components that can be configured to run based on the needs of the user. These configurations are specified in a property file. Below is a description of the various properties that can be set in the [property file](https://github.com/aorogat/Maestro/blob/main/src/main/resources/conf.properties):

### kg_name

This property specifies the name of the knowledge graph that the Maestro system will be working with. Currently, the system supports the following knowledge graphs:

1. DBPedia
2. MAKG
3. NOBEL
4. GEOData
5. DBTune

### Generating Lexicons subsystem

The Maestro system has a subsystem called "Generating Lexicons" which is responsible for extracting various information from the knowledge graph and storing it in the database. The following properties can be set to control the components of this subsystem:

#### Database_Intializer

This property specifies whether the system should initialize the database. If the value is set to "1", the system will prompt the user to confirm whether they want to delete all previous data in the database before initializing it.

#### Predicate_Extractor

This property specifies whether the system should extract predicates from the knowledge graph. If the value is set to "1", the system will run the "Predicate_Extractor" component.

#### NLP_Pattern_Extractor_Text_Corpus
This property specifies whether the system should extract natural language patterns from a text corpus. If the value is set to "1", the system will run the "NLP_Pattern_Extractor" component.

#### Predicate_Representations_Labels
This property specifies whether the system should extract predicate representations from labels in the knowledge graph. If the value is set to "1", the system will run the "Predicate_Representation_Extractor" component to fill the database with predicate representations extracted from labels.

#### Predicate_Representations_Text_Corpus
This property specifies whether the system should extract predicate representations from a text corpus. If the value is set to "1", the system will run the "Predicate_Representation_Extractor" component to fill the database with predicate representations extracted from the text corpus.

### Question types
The Maestro system supports a number of question types, which can be configured to be included in the generated benchmark by setting the corresponding property to "1". The question types that can be generated are:

- What
- Who
- Whose
- Whom
- When
- Where
- Which
- How
- Yes_No
- Request
- Pruned

### Query shapes
The Maestro system can generate a number of different query shapes, which can be configured to be included in the generated benchmark by setting the corresponding property to "1". The query shapes that can be generated are:

- Single_Edge
- Chain
- Star
- Tree
- Flower
- Cycle
- Cycle_General
- Star_Set
- Star_Having
