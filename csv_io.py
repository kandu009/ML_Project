import re

def read_file(file_path, has_header = False):
    with open(file_path) as f:
        if has_header: f.readline()
        data = []
        for line in f:
            line =  split(line,['    ','|','   ',' ','\t'])
            data.append([x for x in line])
    return data

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

    
