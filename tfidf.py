import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import csv_io

class Tfidf:

    def __init__(self,filename1,filename2):
        self.filename1 = filename1
        self.filename2 = filename2
        self.tfidf_vectorizer = TfidfVectorizer(tokenizer=lambda doc: doc,lowercase=False)
        self.train_tfidf()

    def train_tfidf(self):
        self.docs = []
        self.docs1_id = []
        self.docs2_id = []
        docs1 = csv_io.read_file(self.filename1)
        docs2 = csv_io.read_file(self.filename2)
        self.len1 = len(docs1)
        self.len2 = len(docs2)
        for doc1 in docs1:
            self.docs.append(doc1[1:])
            self.docs1_id.append(doc1[0])
        for doc2 in docs2:
            self.docs.append(doc2[1:])
            self.docs2_id.append(doc2[0])
        self.tfidf_matrix = self.tfidf_vectorizer.fit_transform(self.docs).toarray()

    def classify(self,id1,id2):
        ind1 = self.docs1_id.index(id1)
        ind2 = self.len1+self.docs2_id.index(id2)
        return cosine_similarity(self.tfidf_matrix[ind1],self.tfidf_matrix[ind2])
        
 
    

