function addingFeatures_2( )

data_matrix = csvread('ctr_charan_train_test.csv');
[row,col] = size(data_matrix);
%keys are
ctr_sum_ad= containers.Map;
ctr_count_ad = containers.Map;
ctr_sum_adv= containers.Map;
ctr_count_adv = containers.Map;
ctr_sum_depth= containers.Map;
ctr_count_depth = containers.Map;
ctr_sum_pos= containers.Map;
ctr_count_pos = containers.Map;
ctr_sum_rel_pos= containers.Map;
ctr_count_rel_pos = containers.Map;
for i = 1:row
    %add the column for relative depth
    relativeDepth = (data_matrix(i,7) - data_matrix(1,8))/data_matrix(i,7);
    data_matrix(i,col+1) = relativeDepth;
    fprintf('calc for row %d\n',i);
    %ctr by ad id
    key = strcat(num2str(data_matrix(i,5)),'');
    if ctr_count_ad.isKey(key)
        count = ctr_count_ad(key);
        ctr_count_ad(key) = count+1;
        sum = ctr_sum_ad(key);
        ctr_sum_ad(key) = sum + data_matrix(i,1);
    else
        ctr_count_ad(key) = 1;
        ctr_sum_ad(key) = data_matrix(i,1);
    end
    %ctr by adv id
    key = strcat(num2str(data_matrix(i,6)),'');
    if ctr_count_adv.isKey(key)
        count = ctr_count_adv(key);
        ctr_count_adv(key) = count+1;
        sum = ctr_sum_adv(key);
        ctr_sum_adv(key) = sum + data_matrix(i,1);
    else
        ctr_count_adv(key) = 1;
        ctr_sum_adv(key) = data_matrix(i,1);
    end
    %ctr by depth
    key = strcat(num2str(data_matrix(i,7)),'');
    if ctr_count_depth.isKey(key)
        count = ctr_count_depth(key);
        ctr_count_depth(key) = count+1;
        sum = ctr_sum_depth(key);
        ctr_sum_depth(key) = sum + data_matrix(i,1);
    else
        ctr_count_depth(key) = 1;
        ctr_sum_depth(key) = data_matrix(i,1);
    end
    %ctr by position
    key = strcat(num2str(data_matrix(i,8)),'');
    if ctr_count_pos.isKey(key)
        count = ctr_count_pos(key);
        ctr_count_pos(key) = count+1;
        sum = ctr_sum_pos(key);
        ctr_sum_pos(key) = sum + data_matrix(i,1);
    else
        ctr_count_pos(key) = 1;
        ctr_sum_pos(key) = data_matrix(i,1);
    end
    %ctr by relative position
    key = strcat(num2str(data_matrix(i,col+1)),'');
    if ctr_count_rel_pos.isKey(key)
        count = ctr_count_rel_pos(key);
        ctr_count_rel_pos(key) = count+1;
        sum = ctr_sum_rel_pos(key);
        ctr_sum_rel_pos(key) = sum + data_matrix(i,1);
    else
        ctr_count_rel_pos(key) = 1;
        ctr_sum_rel_pos(key) = data_matrix(i,1);
    end
end
for i=1:row 
   %add the new features
   fprintf('appending for row %d\n',i);
   key = strcat(num2str(data_matrix(i,5)),'');
   data_matrix(i,col+2) = ctr_sum_ad(key)/ctr_count_ad(key);
   data_matrix(i,col+3) = (ctr_sum_ad(key) + 0.0375)/(ctr_count_ad(key) + 0.75);
   key = strcat(num2str(data_matrix(i,6)),'');
   data_matrix(i,col+4) = ctr_sum_adv(key)/ctr_count_adv(key);
   data_matrix(i,col+5) = (ctr_sum_adv(key) + 0.0375)/(ctr_count_adv(key) + 0.75);
   key = strcat(num2str(data_matrix(i,7)),'');
   data_matrix(i,col+6) = ctr_sum_depth(key)/ctr_count_depth(key);
   data_matrix(i,col+7) = (ctr_sum_depth(key) + 0.0375)/(ctr_count_depth(key) + 0.75);
   key = strcat(num2str(data_matrix(i,8)),'');
   data_matrix(i,col+8) = ctr_sum_pos(key)/ctr_count_pos(key);
   data_matrix(i,col+9) = (ctr_sum_pos(key) + 0.0375)/(ctr_count_pos(key) + 0.75);
   key = strcat(num2str(data_matrix(i,col+1)),'');
   data_matrix(i,col+10) = ctr_sum_rel_pos(key)/ctr_count_rel_pos(key);
   data_matrix(i,col+11) = (ctr_sum_rel_pos(key) + 0.0375)/(ctr_count_rel_pos(key) + 0.75);
end
%removing unnecessary features.
data_matrix(:,4) = [];
data_matrix(:,4) = [];
data_matrix(:,4) = [];
data_matrix(:,6) = [];
data_matrix(:,6) = [];
data_matrix(:,6) = [];
data_matrix(:,6) = [];
%format long g;
fileId = fopen('ctr_charan_train_test.txt','w');
dlmwrite('ctr_charan_train_test.txt',data_matrix,'delimiter',',','precision',50);
%csvwrite('7_varun_out_training.csv',data_matrix);
fclose(fileId);
end

