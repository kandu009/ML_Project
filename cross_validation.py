def KFold(num_features, num_crossval):
    indices_train = []
    indices_test = []
    split = num_features/num_crossval
    for i in range(num_crossval):
        test = [x for x in range(i*split,(i+1)*split)]
        train = [x for x in range(num_features)]
        for j in test:
            train.remove(j)
        indices_train.append(train)
        indices_test.append(test)
    return (indices_train,indices_test);
