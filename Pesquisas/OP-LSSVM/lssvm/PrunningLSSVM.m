classdef PrunningLSSVM < handle
    
   properties
      train_features % features of training dataset
      train_labels % labels of training dataset
      gamma % trade-off between hit and error costs
      bias % bias of LSSVM classifier
      alphas % Lagrange multipliers of LSSVM classifier
      halphas % history of alphas
      pruning_percs % pecentage of patterns to be prunned
   end
   
   methods
      function obj = PrunningLSSVM(train_features, train_labels, params)
         obj.train_features = train_features;
         obj.train_labels = train_labels;
         obj.gamma = params.gamma;
         obj.bias = [];
         obj.alphas = [];
      end
      
      function train(obj)
        % getting A.x = b problem
        A = (obj.train_features*obj.train_features').*(obj.train_labels*obj.train_labels');
        A = A + (1/obj.gamma)*eye(size(obj.train_features,1));
        A = [obj.train_labels'; A];
        A = [[0; obj.train_labels] A];
        b = ones(size(A,1),1);
        b(1,1) = 0;
        
        
        % liner system solved by inverse
        x = A\b;
        obj.bias = [x(1,1)];
        obj.alphas = [x(2:size(x,1),1)];

        % verify accuracy
        features = obj.train_features;
        labels = obj.train_labels;
        train_accuracy = obj.accuracy(features, labels);
        
        i = 0; 
        epsilon = 0.03;
        numberOfPrunedPatterns = 10;
        while (i <= size(obj.train_features,1)/numberOfPrunedPatterns)
            %put some patterns out  
            obj.bias = [x(1,1)];
            x2 = [ x(2:size(x,1),1) ]; 
            inds = [1:1:size(x2,1)]';
            x2 = [ abs(x2) inds ];
            x2 = sortrows(x2,1);
            indexes = x2(1 : numberOfPrunedPatterns, 2);
                
            A(:,indexes) = [];
            x = (A'*A)\(A'*b);
            obj.train_features(indexes,:) = [];
            obj.train_labels(indexes,:) = [];
            obj.alphas = [x(2:size(x,1),1)];
            % solving based on inverse
            % evaluate accuracy
            hit_rate = obj.accuracy(features, labels);
            if (hit_rate + epsilon < train_accuracy)
                break;
            end    
            i = i + 1;
        end    
      end
      
      function result = accuracy(obj,features,labels)
        output_labels = obj.classify(features);
        result = length(find((output_labels - labels) == 0))/size(labels,1);
      end    
      
      function result = classify(obj,test_features)
        %make decision function
        result = sign(sum(test_features*obj.train_features'.*repmat(obj.alphas'.*obj.train_labels',size(test_features,1),1),2) + obj.bias);
      end
      
   end % methods
end % classdef 