function average_blender( )
%finds mean of the pCTR as a blening method

v1 = importdata('linear_ctr.txt');
v2 = importdata('pegasos_ctr.txt');
v3 = importdata('probit_ctr.txt');

m = size(v1,1);%number of rows
for i = 1:m
    v1(i,2) = (v1(i,2)+v2(i,2)+v3(i,2))/3;
end

fileId = fopen('average_blend.txt', 'w');
dlmwrite('average_blend.txt', v1, 'precision', 10);
fclose(fileId);

end