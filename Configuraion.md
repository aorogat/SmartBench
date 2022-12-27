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

Here is additional information on the query shape parameters in the provided configuration file:

1. Chain: The chainMaxLength parameter specifies the maximum length of the chain in the generated queries. The minimum length is 2. For example, setting chainMaxLength to 3 will generate chain-shaped queries with a maximum length of 3, and the minimum length will be 2.

2. Star: The noOfBranches parameter specifies the number of branches in the star-shaped queries. It should be a list of numbers, such as 1,2,3. For example, setting noOfBranches to 1,2,3 will generate star-shaped queries with 1, 2, or 3 branches.

3. Tree: The rootNoOfBranchesTree parameter specifies the number of branches in the root star of the tree-shaped queries. It should be a list of numbers, such as 1,2,3. For example, setting rootNoOfBranchesTree to 2,3 will generate tree-shaped queries with a root star that has 2 or 3 branches. The number of branches in the second star is fixed at 2 and cannot be customized.

4. Flower: The rootNoOfBranchesFlower parameter specifies the number of branches in the root star of the flower-shaped queries. It should be a list of numbers, such as 1,2,3. For example, setting rootNoOfBranchesFlower to 2,3 will generate flower-shaped queries with a root star that has 2 or 3 branches.
