# encoding: utf-8

# *******************************************************************************
# Copyright (c) 2013 aegif.
# 
# This file is part of NemakiWare.
# 
# NemakiWare is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# NemakiWare is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with NemakiWare.
# If not, see <http://www.gnu.org/licenses/>.
# 
# Contributors:
#     linzhixing - initial API and implementation
# ******************************************************************************
require 'active_cmis'

class ChangeLogSubscription
  def self.authenticate
    nemaki_auth_info = {:id => CONFIG['repository']['admin_id'], :password => CONFIG['repository']['admin_password']}
    @nemaki_repository = NemakiRepository.new(nemaki_auth_info) 
  end

  def self.execute
   authenticate
   @nemaki_repository.cache_changes 
  end
end