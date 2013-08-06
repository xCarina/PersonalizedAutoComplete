# Personalized Auto Complete

##Todo
+ add neo4j jar files
+ write config.txt and configure path in PersonalizedAutoComplete > setup > config.jar)

##Doc: Packages
###search
+ Eval_Averages.java => calculates the average precision and recall for one query and algorithm over all users
+ Evaluation.java => calculates precision and recall for a query, user and algorithm
+ Search.java => executes the different search algorithms
###setup
+ BuildIndex.java => builds the search index
+ Config.java => configures used variables such as path of files
+ CreateGraphDatabase => creates the Neo4j Graph DB
+ CustomTokenAnalyser => Extrension of Analyser
+ PageRank => Calculates the Page Rank for the Graph DB
###start
+ startSearch => starts a search process for a given user, query and algorithm
###userProfile
+ UserProfile => extracts users from file and seperates data for learning and testing
##Doc: Config File:
+ DB_BATH => neo4j Database path
+ WIKI_TITLES => path to wiki-titles file
+ WIKI_LINKS => path to wiki-links file
+ WIKI_USERS => path to wiki-user file
+ INDEX_NAME => name for the searchIndex
+ NUMBER_OF_ITERATIONS => number of iterations used for pagerank calculation
+ PRECISION_K => precision at k
+ USER_CNT => number of users used for search
+ QUERY => search query
+ ALGO_ID => algorithm ID (1|2|3)
+ EVAL_DIR => directory to store evaluation values
