import cherrypy
from nltk.corpus import wordnet as wn
import nltk
#nltk.download()   #MAKE SURE THIS COMMAND RUN FOR ONE TIME
from nltk.stem.wordnet import WordNetLemmatizer
import spacy
import inflect

class HelloJson(object):
    @cherrypy.expose
    def similarity(self, word1, word2):
        try:
            # https://stackoverflow.com/questions/45310409/using-a-word2vec-model-pre-trained-on-wikipedia
            #modelPath = "./200/model.txt"
            # word_vectors = gensim.models.KeyedVectors.load_word2vec_format(modelPath, binary=True)
            #w1 = word_vectors['develop_VERB']
            #w2 = word_vectors['create_VERB']

            #w1 = word_vectors[word1]

            #w2 = word_vectors[word2]

            #sim = dot(w1, w2) / (norm(w1) * norm(w2))
            #print('sim=', sim)
            #return str(sim)
            return '0'
        except:
            return '0'

    @cherrypy.expose
    def type(self, word):
        nltk.download('wordnet')
        pos = wn.synsets(word)
        posString = ''
        for p in pos:
            posString += p.name().split(".")[1]
        return "".join(set(posString))

    @cherrypy.expose
    def base(self, word):
        nltk.download('wordnet')
        return WordNetLemmatizer().lemmatize(word,'v')

    @cherrypy.expose
    def phraseSimilarity(self, phrase1, phrase2):
        try:
            nlp = spacy.load("en_core_web_lg")
        except:
            spacy.cli.download("en_core_web_lg")
            nlp = spacy.load("en_core_web_lg")

        doc1 = nlp(u''+phrase1)
        doc2 = nlp(u''+phrase2)
        sim = doc1.similarity(doc2)
        print(sim)
        return str(sim)

    @cherrypy.expose
    def plural(self, word):
        p = inflect.engine()
        return p.plural(word)

if __name__ == '__main__':
    cherrypy.config.update({'server.socket_port':12311})
    cherrypy.quickstart(HelloJson())