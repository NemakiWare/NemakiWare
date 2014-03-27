 Nemakishare::Application.routes.draw do
  root :to => 'nodes#index'
    
  resources :nodes do
    collection do 
      get 'explore'
      get 'search'
      post 'authenticate'
      get 'logout'
    end
    member do
      get 'explore'
      get 'edit_upload'
      put 'upload'
      get 'download'
      put 'move'
      get 'edit_permission'
      post 'update_permission'
    end
  end
  
  resources :users do
    collection do
      get 'search'
      get 'search_both'
    end
    member do
      get 'edit_password'
      put 'update_password'
    end    
  end
  
  resources :groups do
    collection do
      get 'search'  
    end
    
    member do
      get 'edit_member_users'
      put 'update_member_users'
      get 'edit_member_groups'
      put 'update_member_groups'
    end
  end
  
  resources :archives do
    member do
      put 'restore'
    end
  end

  resources :sites
  
  resources :search_engine
  
  resources :types

  match 'principals/search' => 'principals#search'
  

  # The priority is based upon order of creation:
  # first created -> highest priority.

  # Sample of regular route:
  #   match 'products/:id' => 'catalog#view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   match 'products/:id/purchase' => 'catalog#purchase', :as => :purchase
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Sample resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Sample resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Sample resource route with more complex sub-resources
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', :on => :collection
  #     end
  #   end

  # Sample resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end

  # You can have the root of your site routed with "root"
  # just remember to delete public/index.html.
  # root :to => 'welcome#index'

  # See how all your routes lay out with "rake routes"

  # This is a legacy wild controller route that's not recommended for RESTful applications.
  # Note: This route will make all actions in every controller accessible via GET requests.
  # match ':controller(/:action(/:id))(.:format)'
end
