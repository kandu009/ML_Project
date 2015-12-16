function acu_weighted_blender( )
%finds harmonic mean of the pCTR as a blening method

v1 = importdata('linear_ctr.txt');
v2 = importdata('pegasos_ctr.txt');
v3 = importdata('probit_ctr.txt');
w1 = 0.69;
w2 = 0.73;
w3 = 0.8;
w_total = w1+w2+w3;

m = size(v1,1);
for i = 1:m
    v1(i,2) = v1(i,2)*(w1/w_total) + v2(i,2)*(w2/w_total) + v3(i,2)*(w3/w_total);
end

fileId = fopen('acu_weighted_blend.txt', 'w');
dlmwrite('acu_weighted_blend.txt', v1, 'precision', 10);
fclose(fileId);

end

