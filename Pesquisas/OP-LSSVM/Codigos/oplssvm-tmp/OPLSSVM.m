classdef OPLSSVM < handle
    
   properties
      train_features % features of training dataset
      train_labels % labels of training dataset
      gamma % trade-off between hit and error costs
      bias % bias of LSSVM classifier
      alphas % Lagrange multipliers of LSSVM classifier
      halphas % history of alphas
      pruning_percs % pecentage of patterns to be prunned
      
      max_sv %Max number of suport vectors
      cadidate_sv %Suport Vectors Indexes
   end
   
   methods
      function obj = OPLSSVM(train_features, train_labels, params)
         obj.train_features = train_features;
         obj.train_labels = train_labels;
         obj.gamma = params.gamma;
         obj.bias = [];
         obj.alphas = [];
         obj.max_sv = params.max_sv;
      end
      
      function [train_accuracy] = train(obj)
        % getting A.x = b problem
        A = (obj.train_features*obj.train_features').*(obj.train_labels*obj.train_labels');
        A = A + (1/obj.gamma)*eye(size(obj.train_features,1));
         
        %checking mrsr
        [W,indexes] = mrsr(obj.train_labels,A,obj.max_sv);       
        obj.cadidate_sv = indexes;
        
        %Preservando variáveis
        Ao = A;
        indexes_o = indexes;
        train_features_o = obj.train_features;
        train_labels_o = obj.train_labels;        
        
        
        count = 1;
        step = 2;
        train_accuracy = zeros(2, ceil(length(indexes_o)/step) );
        for i=1:step:length(indexes_o)
            
            indexes = indexes_o(1:i);
            A = Ao(:,indexes);

            %adding upper and left info
            A = [train_labels_o(indexes)'; A];
            A = [[0; train_labels_o] A];
            b = ones(size(A,1),1);
            b(1,1) = 0;


            if (0)
                x = A\b;
            else    
                x = (A'*A)\(A'*b);
            end


            obj.bias = [x(1,1)];
            obj.alphas = [x(2:size(x,1),1)];

            obj.train_features = train_features_o(indexes,:);
            obj.train_labels = train_labels_o(indexes,:);            


            % verify accuracy
            %features = obj.train_features;
            %labels = obj.train_labels;
            
            train_accuracy(1,count) = obj.accuracy(train_features_o, train_labels_o);
            train_accuracy(2,count) = i;
            count = count + 1;
        end

            [v id] = max(train_accuracy);
            
            
            %% %ajustando para valores ótimos
            indexes = indexes_o(1:id*step);
            A = Ao(:,indexes);

            %adding upper and left info
            A = [train_labels_o(indexes)'; A];
            A = [[0; train_labels_o] A];
            b = ones(size(A,1),1);
            b(1,1) = 0;


            if (1)
                x = A\b;
            else    
                x = (A'*A)\(A'*b);
            end


            obj.bias = [x(1,1)];
            obj.alphas = [x(2:size(x,1),1)];

            obj.train_features = train_features_o(indexes,:);
            obj.train_labels = train_labels_o(indexes,:);    
            
      end
      
      function result = accuracy(obj,features,labels)
        output_labels = obj.classify(features);
        result = length(find((output_labels - labels) == 0))/size(labels,1);
      end    
      
      function result = classify(obj,test_features)
        %make decision function
        result = sign(sum(test_features * obj.train_features'.* repmat(obj.alphas' .* obj.train_labels',size(test_features,1),1),2) + obj.bias);
      end
      
   end % methods
end % classdef 