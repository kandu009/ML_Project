from __future__ import division
from sklearn import datasets, linear_model
import numpy as np
import re
import math
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt
import sys
import random

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

def generate(min = 0, max = 0.5, number = 100):
	start = 0
	for i in xrange( number, 0, -1 ):
		start = start + math.log( random.random( ) ) / i
		next = math.exp( start ) * ( max - min ) + min
		yield next

def show_roc(train_targets, test_targets, pred_train, pred_test, fold):
   
	roc_train_labels = []
	for t in train_targets:
		if t > 0.0:
			roc_train_labels.append(1)
		else:
			roc_train_labels.append(0)
	roc_test_labels = []
	for t in test_targets:
		if t > 0.0:
			roc_test_labels.append(1)
		else:
			roc_test_labels.append(0)

	print 'SCORE, ', roc_auc_score(roc_train_labels, pred_train), ', ', roc_auc_score(roc_test_labels, pred_test)

	# plots
	train_fpr, train_tpr, thresholds = roc_curve(roc_train_labels, pred_train)
	# roc_auc = auc(train_fpr, train_tpr)
	# plt.plot(train_fpr, train_tpr, lw=1, label='ROC fold %d (area = %0.2f)' % (fold, roc_auc))
	# plt.xlabel('False Positive Rate')
	# plt.ylabel('True Positive Rate')
	# plt.title('Receiver operating characteristic example')
	# plt.legend(loc="lower right")
	# plt.show()
	for i in range(len(train_tpr)):
		print 'train,',fold,',',train_fpr[i],',',train_tpr[i]
	
	# # test data
	test_fpr, test_tpr, thresholds = roc_curve(roc_test_labels, pred_test)
	# roc_auc = auc(test_fpr, test_tpr)
	# plt.plot(test_fpr, test_tpr, lw=1, label='ROC fold %d (area = %0.2f)' % (fold, roc_auc))
	# plt.xlabel('False Positive Rate')
	# plt.ylabel('True Positive Rate')
	# plt.title('Receiver operating characteristic example')
	# plt.legend(loc="lower right")
	# plt.show()
	for i in range(len(test_tpr)):
		print 'test,',fold,',',test_fpr[i],',',test_tpr[i]

def logistic():

	data = read_train(sys.argv[1])
	features = np.array([x[1:] for x in data])
	features=np.array(features,dtype=float)
	targets = np.array([x[0] for x in data])
	target_labels = []

	for x in targets:
		target_labels.append(float(x))
	
	targets = np.array(target_labels)
	num_samples = len(data)
	num_features = len(data[0])-1
	(train_indices,test_indices) = KFold(num_samples,10)
	fold = 0 

	train_errors = []
	test_errors = []

	for current_train,current_test in zip(train_indices,test_indices):

		fold+=1
		train_features = features[current_train]
		train_targets = targets[current_train]

		test_features = features[current_test]
		test_targets = targets[current_test]

		linreg = linear_model.LinearRegression()
		linreg.fit(train_features, train_targets)
		pred_train = linreg.predict(train_features)
		pred_test = linreg.predict(test_features)

		train_error = math.sqrt(sum((pred_train - train_targets) ** 2))/len(train_features)
		train_errors.append(train_error)

		test_error = math.sqrt(sum((pred_test - test_targets) ** 2))/len(test_features)
		test_errors.append(test_error)

		show_roc(train_targets, test_targets, pred_train, pred_test, fold)

	print train_errors
	print test_errors

logistic()
