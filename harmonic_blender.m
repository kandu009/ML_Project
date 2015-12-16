function harmonic_blender( )
%finds harmonic mean of the pCTR as a blening method

v1 = importdata('linear_ctr.txt');
v2 = importdata('pegasos_ctr.txt');
v3 = importdata('probit_ctr.txt');

m = size(v1,1);
for i = 1:m
    v1(i,2) = 3/(1/v1(i,2) + 1/v2(i,2) + 1/v3(i,2));
end

fileId = fopen('harmonic_blend.txt', 'w');
dlmwrite('harmonic_blend.txt', v1, 'precision', 10);
fclose(fileId);

end

