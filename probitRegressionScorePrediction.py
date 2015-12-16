import numpy as np
import sys
from scipy.stats import norm
from collections import namedtuple
import random
import math
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt

MAX_ABS_SURPRISE = 5.0

def gaussian_corrections(t):
	# Clipping avoids numerical issues from ~0/~0.
	t = np.clip(t, -MAX_ABS_SURPRISE, MAX_ABS_SURPRISE)
	v = norm.pdf(t) / norm.cdf(t)
	w = v * (v + t)
	return (v, w)

def read_input_data(data):
	result_data = []
	for row in data:
		r = [float(x) for x in row.split(",")]
		result_data.append(r)
	return result_data

def get_kfold_data(training_data, number_of_crossvalidations):
	shuffled_indices = np.random.permutation(len(training_data))
	split_index = int(((number_of_crossvalidations-1)*len(training_data))/(number_of_crossvalidations))
	train_indices = shuffled_indices[:split_index]
	test_indices = shuffled_indices[split_index+1:]
	folded_train_data = [ training_data[i] for i in train_indices ]
	folded_test_data = [ training_data[i] for i in test_indices ]
	return folded_train_data, folded_test_data

class Feature(object):
	def __init__(self, feature, value):
		self._feature = feature
		self._value = value

class Gaussian(object):
	def __init__(self, mean, variance):
		self._mean = mean
		self._variance = variance

def bias_feature():
	return Feature(feature=0, value=0)

def prior_bias_weight(prior_probability, beta, num_features):
	bias_mean = norm.ppf(prior_probability) * (beta ** 2 + num_features)
	return Gaussian(mean=bias_mean, variance=1.0)

def gaussian_corrections(t):
	t = np.clip(t, -MAX_ABS_SURPRISE, MAX_ABS_SURPRISE)
	v = norm.pdf(t) / norm.cdf(t)
	w = v * (v + t)
	return (v, w)

def prior_weight():
	return Gaussian(mean=0.0, variance=1.0)

def serialize_feature(feature):
	return str(feature._feature)+'_'+str(feature._value)

def deserialize_feature(string):
	r = [float(x) for x in row.split("_")]
	assert len(r) == 2
	f = Feature(feature=int(r[0]), value=float(r[1]))
	return f

def kl_divergence(p, q):
	return p * np.log(p / q) + (1.0 - p) * np.log((1.0 - p) / (1.0 - q))

def _create_feature_vector(feature_vector):
	x_result = [bias_feature()]
	i = 0
	for feature in feature_vector:
		if i == 0:
			y_result = float(feature_vector[0])
		else:
			x_result.append(Feature(feature=i, value=feature))
		i = i+1
	return x_result, y_result

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

	print 'SCORE,', fold, ',', roc_auc_score(roc_train_labels, pred_train), ', ', roc_auc_score(roc_test_labels, pred_test)

	# plots
	# train_fpr, train_tpr, thresholds = roc_curve(roc_train_labels, pred_train)
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
	# test_fpr, test_tpr, thresholds = roc_curve(roc_test_labels, pred_test)
	# roc_auc = auc(test_fpr, test_tpr)
	# plt.plot(test_fpr, test_tpr, lw=1, label='ROC fold %d (area = %0.2f)' % (fold, roc_auc))
	# plt.xlabel('False Positive Rate')
	# plt.ylabel('True Positive Rate')
	# plt.title('Receiver operating characteristic example')
	# plt.legend(loc="lower right")
	# plt.show()
	for i in range(len(test_tpr)):
		print 'test,',fold,',',test_fpr[i],',',test_tpr[i]

class AdPredictor(object):
	
	Config = namedtuple(
		'Config',
		['beta', 'prior_probability', 'epsilon', 'num_features'])

	def __init__(self, config):
		self._config = config
		self._weights = {}
		bias_weight = prior_bias_weight(
			config.prior_probability,
			config.beta,
			config.num_features)
		self._set_weight(bias_feature(), bias_weight)
	
	def _active_mean_variance(self, features):
		means = (self._get_weight(f)._mean for f in features)
		variances = (self._get_weight(f)._variance for f in features)
		return sum(means), sum(variances) + self._config.beta ** 2

	def _get_weight(self, feature):
		return self._weights.get(serialize_feature(feature), prior_weight())

	def _set_weight(self, feature, weight):
		assert not np.isnan(weight._mean)
		assert weight._variance >= 0.0
		self._weights[serialize_feature(feature)] = weight
	
	def predict_score(self, features):
		assert len(features) == self._config.num_features
		total_mean, total_variance = self._active_mean_variance(features)
		w_transpose_x = 0.0
		for f in features:
			w_transpose_x = w_transpose_x+self._get_weight(f)._mean*f._value
		return norm.cdf(float(total_mean)/float(total_variance))

	def train(self, features, label):
		assert len(features) == self._config.num_features
		y = float(label)
		total_mean, total_variance = self._active_mean_variance(features)
		v, w = gaussian_corrections(y * total_mean / np.sqrt(total_variance))
		for feature in features:
			weight = self._get_weight(feature)
			mean_delta = y * weight._variance / np.sqrt(total_variance) * v
			variance_multiplier = 1.0 - weight._variance / total_variance * w
			updated = Gaussian(mean=weight._mean+mean_delta, variance=weight._variance*variance_multiplier)
			self._set_weight(feature, self._apply_dynamics(updated))

	@property
	def weights(self):
		return [(deserialize_feature(f), w)
				for (f, w) in self._weights.iteritems()]

	def _apply_dynamics(self, weight):
		prior = prior_weight()
		adjusted_variance = weight._variance * prior._variance / \
			((1.0 - self._config.epsilon) * prior._variance +
			 self._config.epsilon * weight._variance)
		adjusted_mean = adjusted_variance * (
			(1.0 - self._config.epsilon) * weight._mean / weight._variance +
			self._config.epsilon * prior._mean / prior._variance)
		adjusted = Gaussian(mean=adjusted_mean, variance=adjusted_variance)
		return adjusted

if __name__ == '__main__':
	if len(sys.argv) != 3:
		print 'Invalid arguments, run using: python online_probit_regression.py <dataset_filename> <num_of_crossvalidations>'
		sys.exit(1)
	else:
		filename = sys.argv[1]
		num_of_crossvalidations = sys.argv[2]
		training_data = read_input_data(open(filename))
		num_features = len(training_data[0])
		train_errors = []
		test_errors = []
		for k in range(int(num_of_crossvalidations)):
			(folded_train_data, folded_test_data) = get_kfold_data(training_data, int(num_of_crossvalidations))
			predictor_config=AdPredictor.Config(
				beta=0.05,
				prior_probability=0.5,
				epsilon=0.5,
				num_features=num_features)
			predictor = AdPredictor(predictor_config)
			
			for feature_vector in folded_train_data:
				proto_features, proto_label = _create_feature_vector(feature_vector)
				predictor.train(proto_features, proto_label)
			
			train_error = 0.0
			test_error = 0.0
			
			# train set error.
			train_targets = []
			pred_train = []
			for feature_vector in folded_train_data:
				proto_features, proto_label = _create_feature_vector(feature_vector)
				predicted_label = predictor.predict_score(proto_features)
				train_error = train_error+(abs(predicted_label-proto_label)*abs(predicted_label-proto_label))
				train_targets.append(proto_label)
				pred_train.append(predicted_label)
			train_errors.append(float(math.sqrt(train_error))/float(len(folded_train_data)))

			# test set error.
			test_targets = []
			pred_test = []
			for feature_vector in folded_test_data:
				proto_features, proto_label = _create_feature_vector(feature_vector)
				predicted_label = predictor.predict_score(proto_features)
				test_error = test_error+(abs(predicted_label-proto_label)*abs(predicted_label-proto_label))
				test_targets.append(proto_label)
				pred_test.append(predicted_label)
			test_errors.append(float(math.sqrt(test_error))/float(len(folded_test_data)))

			show_roc(train_targets, test_targets, pred_train, pred_test, k)

		print train_errors
		print test_errors