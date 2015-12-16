from __future__ import division
from sklearn import datasets, linear_model
import numpy as np
import re
import math
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt
import sys

def show_roc(fold, fpr, tpr, testOrTrain):
	#print fpr
	#print tpr
	roc_auc = auc(fpr, tpr)
	if testOrTrain == 'test':
		label = 'ROC for Test fold %d (area = %0.3f)'
	else:
		label = 'ROC for Train fold %d (area = %0.3f)'
	plt.plot(fpr, tpr, lw=1, label=label % (fold, roc_auc))
	plt.axis([0,1,0,1])
	plt.xlabel('False Positive Rate')
	plt.ylabel('True Positive Rate')
	plt.title('AUC Plot')
	plt.legend(loc="lower right")
	plt.show()
	# print roc_auc
	for i in range(len(tpr)):
		print testOrTrain,',',fold,',',fpr[i],',',tpr[i]



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

def plotAUC():
	#read the tpr,fpr data from the file
	data = read_train(sys.argv[1])
	testOrTrain = 'train'
	start = True
	fpr = []
	tpr = []
	fold = 1
	for sample in data:
		#print sample[0]
		#print sample[1]
		if not sample[0] == testOrTrain or not fold == int(sample[1]):
			if not start:
				#print testOrTrain, ' fold ', fold 
				# if fold == 5:
				show_roc(fold, fpr, tpr, testOrTrain)
			else:
				start = False #first time do nothing
			if sample[0] == 'test':
				testOrTrain = 'test'
			else:
				testOrTrain = 'train'
			fold = int(sample[1])
			fpr = []
			tpr = []
			fpr.append(float(sample[2]))
			tpr.append(float(sample[3]))
		else:
			#print sample[2]
			fpr.append(float(sample[2]))
			tpr.append(float(sample[3]))

	show_roc(fold, fpr, tpr, testOrTrain)

plotAUC()