import csv
import math
import numpy
import sys
import calendar
import time
import random
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt

# Lambda used for computation.
lambda_value = 2

# Given a input k, it returns k/2 or ~equal number of samples from each of the classes.
def get_percent_of_training_set(class0_size, class1_size, k, total_dataset_size):
	shuffled_indices0 = numpy.random.permutation(class0_size)
	split_index0 = int(k/2)
	current_train0_indices = shuffled_indices0[:split_index0]
	shuffled_indices1 = numpy.random.permutation(class1_size)
	split_index1 = int(k-(k/2))
	current_train1_indices = shuffled_indices1[:split_index1]
	return current_train0_indices, current_train1_indices

# Given a input k, it returns k/2 or ~equal number of samples from each of the classes.
def get_separate_train_test_set(class0_size, class1_size, k, total_dataset_size):
	shuffled_indices0 = numpy.random.permutation(class0_size)
	split_index0 = int(k/2)
	current_train0_indices = shuffled_indices0[:split_index0]
	current_test0_indices = shuffled_indices0[split_index0:]
	shuffled_indices1 = numpy.random.permutation(class1_size)
	split_index1 = int(k-(k/2))
	current_train1_indices = shuffled_indices1[:split_index1]
	current_test1_indices = shuffled_indices1[split_index1:]
	return current_train0_indices, current_train1_indices, current_test0_indices, current_test1_indices

# Computes the W as per the algorithm mentioned in Figure1 in the suggested reference Pegasos paper.
def compute_pegasos_w(X_class0, y_class0, X_class1, y_class1, k, max_iterations, total_dataset_size, number_of_features):

	global lambda_value
	number_of_features = X_class0.shape[1]
	# Initialize W to all zeros
	W = numpy.zeros(shape=(1, number_of_features))
	W = numpy.array(W, dtype=float)

	for t in range(max_iterations):
		
		X_At = []
		y_At = []

		# We split the data such that we have equal 
		# percentage of data from each of the classes.
		current_train0_indices, current_train1_indices = get_percent_of_training_set(y_class0.shape[0], y_class1.shape[0], k, total_dataset_size)
		trainset_size = current_train0_indices.shape[0]+current_train1_indices.shape[0]
		X_At = numpy.concatenate((X_class0[current_train0_indices,:],X_class1[current_train1_indices,:]))
		X_At = numpy.array(X_At, dtype=float)
		y_At = numpy.concatenate((y_class0[current_train0_indices,:],y_class1[current_train1_indices,:]))
		y_At = numpy.array(y_At, dtype=float)   

		X_AtPlus = []
		y_AtPlus = []
		size = 0
		# Compute all the At+ feature vectors
		for i in range(y_At.shape[0]):
			if y_At[i]*numpy.dot(W, X_At[i,:]) < 1:
				X_AtPlus.append(X_At[i,:])
				y_AtPlus.append(y_At[i])
				size = size+1
		if size == 0:
			continue
		X_AtPlus = numpy.array(X_AtPlus, dtype=float)
		y_AtPlus = numpy.array(y_AtPlus, dtype=float)

		# Update the W matrix as per the psuedo code in Figure1 from suggested reference Pegasos.
		eta_t = 1.0/float(lambda_value*(t+1))
		W_t_and_half = (1-(eta_t*lambda_value))*W
		multiplier = float(eta_t/k*1.0)
		for j in range(X_AtPlus.shape[0]):
			ya_ti = []
			ya_ti[:] = [multiplier*y_AtPlus[j]*x for x in X_AtPlus[j,:]]
			ya_ti = numpy.reshape(ya_ti, (1, number_of_features))
			W_t_and_half = W_t_and_half+ya_ti

		W_new = min(1, float(1.0/math.sqrt(lambda_value))/float(numpy.linalg.norm(W_t_and_half)))*W_t_and_half
		if numpy.linalg.norm(W_new-W) < 0.001:
			print 'Converged !, exiting before max iterations.'
			return W
		else:
			W = W_new

	return W

# Predicts the error for given feature
def predict(feature_vector, W, y_true):
	# return numpy.dot(W, feature_vector)/(numpy.linalg.norm(W)*numpy.linalg.norm(feature_vector))
	return numpy.dot(W, feature_vector)

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

# Main method which needs to be invoked for running the algorithm.
def mysgdsvm(file_name, k, num_of_folds):
	
	data = csv.reader(open(file_name))
	X = []
	y = []
	# extract all input features.
	number_of_columns = 0
	for row in data:
		number_of_columns = len(row)
		X.append(row[1:number_of_columns])
		y.append(row[0])

	number_of_features = number_of_columns-1
	total_dataset_size = sum(1 for line in open(file_name));

	y = numpy.array(y, dtype=float)   
	X = numpy.array(X, dtype=float)
   
	# Run the algorithm for #num_of_folds argument.
	max_iterations = 10000
	train_errors = []
	test_errors = []
	for fold in range(num_of_folds):
	
		# print 'Running iteration: {', i, '}'

		# Split them based on class labels.
		num_of_class1_samples = len(numpy.where(y != 0.0)[0])
		tempX1 = numpy.reshape(X[numpy.where(y != 0.0)[0]], (num_of_class1_samples, number_of_features))
		tempy1 = numpy.reshape(y[numpy.where(y != 0.0)[0]], (num_of_class1_samples, 1))
		num_of_class0_samples = total_dataset_size-num_of_class1_samples
		tempX0 = numpy.reshape(X[numpy.where(y == 0.0)[0]], (num_of_class0_samples, number_of_features))
		tempy0 = numpy.reshape(y[numpy.where(y == 0.0)[0]], (num_of_class0_samples, 1))

		current_train0_indices, current_train1_indices, current_test0_indices, current_test1_indices = get_separate_train_test_set(tempy0.shape[0], tempy1.shape[0], k, total_dataset_size)
		X_train0 = tempX0[current_train0_indices,:]
		y_train0 = tempy0[current_train0_indices,:]
		X_train1 = tempX1[current_train1_indices,:]
		y_train1 = tempy1[current_train1_indices,:]
		trainset_size = y_train0.shape[0]+y_train1.shape[0]

		X_test0 = tempX0[current_test0_indices,:]
		y_test0 = tempy0[current_test0_indices,:]
		X_test1 = tempX1[current_test1_indices,:]
		y_test1 = tempy1[current_test1_indices,:]

		W = compute_pegasos_w(X_train0, y_train0, X_train1, y_train1, k, max_iterations, trainset_size, number_of_features)
		
		train_targets = []
		pred_train = []
		test_targets = []
		pred_test = []

		# train set error.
		i = 0
		for (ytrain0, feature_vector) in zip(y_train0, X_train0):
			pred_train.append(predict(feature_vector, W, ytrain0))
			train_targets.append(ytrain0)
			i = i+1
		i = 0
		for (ytrain1, feature_vector) in zip(y_train1, X_train1):
			pred_train.append(predict(feature_vector, W, ytrain1))
			train_targets.append(ytrain1)
			i = i+1
		train_errors.append(math.sqrt(sum([abs(pt-tt) ** 2 for (pt, tt) in zip(pred_train, train_targets)]))/float(len(pred_train)))

		# test set error.
		i = 0
		for (ytest0, feature_vector) in zip(y_test0, X_test0):
			pred_test.append(predict(feature_vector, W, ytest0))
			test_targets.append(ytest0)
			i = i+1
		i = 0
		for (ytest1, feature_vector) in zip(y_test1, X_test1):
			pred_test.append(predict(feature_vector, W, ytest1))
			test_targets.append(ytest1)
			i = i+1
		
		test_errors.append(math.sqrt(sum([abs(pt-tt) ** 2 for (pt, tt) in zip(pred_test, test_targets)]))/float(len(pred_test)))
		
		show_roc(train_targets, test_targets, pred_train, pred_test, fold)

	print train_errors
	print test_errors

# Main program execution.
if len(sys.argv) != 4:
	print 'Invalid arguments passed, run using: python 3.py <dataset_filename> <k> <num_of_folds>'
	sys.exit(1)
else:
	mysgdsvm(sys.argv[1], int(sys.argv[2]), int(sys.argv[3]))