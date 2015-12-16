from __future__ import division
from sklearn import datasets, linear_model
import numpy as np
import re
import math
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt
import sys

def KFold(num_features, num_crossval):
    indices_train = []
    indices_test = []
    split = int(num_features/num_crossval)
    for i in range(num_crossval):
        test = [x for x in range(i*split,(i+1)*split)]
        train = [x for x in range(num_features)]
        for j in test:
            train.remove(j)
        indices_train.append(train)
        indices_test.append(test)
    return (indices_train,indices_test);

def read_train(file_path, has_header = False):
    with open(file_path) as f:
        if has_header: f.readline()
        data = []
        for line in f:
            line =  split(line,[','])
            data.append([x for x in line])
    return data

def split(txt, seps):
    default_sep = seps[0]
    # we skip seps[0] because that's the default seperator
    for sep in seps[1:]:
        txt = txt.replace(sep, default_sep)
    return [i.strip() for i in txt.split(default_sep)]

def show_roc(fold, targets, pred):
    # print 'fold : ',fold
    # print 'Size of targets : ',len(targets)
    # print 'Size of predictions : ', len(pred)
    roc_labels = []
    for t in targets:
        if t > 0.0:
            roc_labels.append(1)
        else:
            roc_labels.append(0)
    print roc_auc_score(roc_labels, pred)
    # plots
    fpr, tpr, thresholds = roc_curve(roc_labels, pred)
    
    roc_auc = auc(fpr, tpr)
    # print fpr, ' , ', tpr, ' , ', roc_auc
    print fold,' , ',roc_auc
    
    plt.plot(fpr, tpr, lw=1, label='ROC fold %d (area = %0.2f)' % (fold, roc_auc))
    plt.axis([0,1,0,1])
    plt.xlabel('False Positive Rate')
    plt.ylabel('True Positive Rate')
    plt.title('Receiver operating characteristic example')
    plt.legend(loc="lower right")
    plt.show()

def plot_blended_roc():

    data = read_train(sys.argv[1])#this is the blended data
    data1 = read_train(sys.argv[2])
    targets = np.array([x[0] for x in data1])
    target_labels = []
    pred = []
    for x in targets:
        target_labels.append(float(x))
    
    targets = np.array(target_labels)
    start = True
    fold = 0
    for sample in data:
        if not fold == int(sample[0]):
            if not start:
                show_roc(fold, targets, pred)
            else:
                start = False #first time do nothing
            fold = int(sample[0])
            pred = []
            pred.append(float(sample[1]))
        else:
            pred.append(float(sample[1]))

    show_roc(fold, targets, pred)

plot_blended_roc()
