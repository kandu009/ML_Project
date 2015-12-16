"""
Query
keyword
ad title
ad description
Query <-> keyword
Query <-> ad title
Query <-> ad description
keyword <-> ad title
keyword <-> ad description
ad title <-> ad description
"""
import csv_io
import numpy as np
import sys
from tfidf import Tfidf

def dummy():
    id1 = '1'
    id2 = '3'
    tfidf = Tfidf("dummy.txt","dummy2.txt")
    return tfidf.classify(id1,id2)

def convert_to_tfidf(filename):
    with open(filename,"w+") as f:
        query_keyword = Tfidf("query_ids.txt","keyword_ids.txt")
        query_title = Tfidf("query_ids.txt","title_ids.txt")
        query_description = Tfidf("query_ids.txt","desc_ids.txt")
        keyword_title = Tfidf("keyword_ids.txt","title_ids.txt")
        keyword_description = Tfidf("keyword_ids.txt","desc_ids.txt")
        title_description = Tfidf("title_ids.txt","desc_ids.txt")
        data = csv_io.read_train("10percent_5lakh_preprocessed_training_data.txt")
        count = 0
        with open("2lakh_training_data.txt") as f1:
            for line in f1:
                count = count + 1
                sample = csv_io.split(line,[','])
                queryid = sample[7]
                keywordid = ''+sample[8]
                titleid = ''+sample[9]
                descriptionid = ''+sample[10]
                qk_sim = query_keyword.classify(queryid,keywordid)
                qt_sim = query_title.classify(queryid,titleid)
                qd_sim = query_description.classify(queryid,descriptionid)
                kt_sim = keyword_title.classify(keywordid,titleid)
                kd_sim = keyword_description.classify(keywordid,descriptionid)
                td_sim = title_description.classify(titleid,descriptionid)
                sample.append('%.2f' % qk_sim[0][0])
                sample.append('%.2f' % qt_sim[0][0])
                sample.append('%.2f' % qd_sim[0][0])
                sample.append('%.2f' % kt_sim[0][0])
                sample.append('%.2f' % kd_sim[0][0])
                sample.append('%.2f' % td_sim[0][0])
                f.write(",".join(sample))
                f.write("\n")
        
convert_to_tfidf("outputfile2.txt")       
