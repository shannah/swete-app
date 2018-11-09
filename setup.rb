#!/usr/bin/env ruby
if not File.file? '.projects'
	abort("No .projects file found")
end

def update(repo_url, path)
  raise 'Update requires url and path.' unless repo_url && path
  if File.directory? path
    puts "Updating #{path} with repo #{repo_url}"
    `git -C #{path} pull origin master`
  else
    puts "Cloning #{repo_url} to #{path}"
    `git clone #{repo_url} #{path}`
  end
end

open('.projects') do |f|
	f.readlines.each do |l|
	  repo_url, path = l.split(/\s+/)
	  update(repo_url, path)
	end
end
