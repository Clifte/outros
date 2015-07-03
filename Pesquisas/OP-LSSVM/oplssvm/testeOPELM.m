ds = DataSet('column_2C.dat');

[Xsub,idx]=licols(ds.features,10E-10);



train_percs = 80;

[train_features train_labels test_features test_labels] = ds.shuffle(train_percs);

train_features=train_features(:,idx);
test_features=test_features(:,idx);

data = {};
data.y = train_labels;
data.x = train_features;




[model] = train_OPELM(data,'g',100,'c','y')



data = {};
data.y = test_labels;
data.x = test_features;
[yh,error] = sim_OPELM(model,data)